#!/usr/bin/env python3
"""Read WORLD_SURFACE heightmaps straight out of a 1.16.5 region file.

Avoids needing a Minecraft client to see what terrain got generated. 1.16+ packs
heightmaps as 9-bit values in longs without spanning long boundaries, so 7 values
sit in each long and the top bit is padding.
"""
import sys, zlib, struct, io


def read_nbt(buf):
    def rd_name():
        (n,) = struct.unpack(">H", buf.read(2))
        return buf.read(n).decode("utf-8", "replace")

    def rd(tag):
        if tag == 0:
            return None
        if tag == 1:
            return struct.unpack(">b", buf.read(1))[0]
        if tag == 2:
            return struct.unpack(">h", buf.read(2))[0]
        if tag == 3:
            return struct.unpack(">i", buf.read(4))[0]
        if tag == 4:
            return struct.unpack(">q", buf.read(8))[0]
        if tag == 5:
            return struct.unpack(">f", buf.read(4))[0]
        if tag == 6:
            return struct.unpack(">d", buf.read(8))[0]
        if tag == 7:
            (n,) = struct.unpack(">i", buf.read(4))
            return buf.read(n)
        if tag == 8:
            (n,) = struct.unpack(">H", buf.read(2))
            return buf.read(n).decode("utf-8", "replace")
        if tag == 9:
            (et,) = struct.unpack(">b", buf.read(1))
            (n,) = struct.unpack(">i", buf.read(4))
            return [rd(et) for _ in range(n)]
        if tag == 10:
            out = {}
            while True:
                (t,) = struct.unpack(">b", buf.read(1))
                if t == 0:
                    return out
                # Name must be read before the value: in "out[rd_name()] = rd(t)"
                # Python evaluates the right-hand side first and swaps the reads.
                key = rd_name()
                out[key] = rd(t)
        if tag == 11:
            (n,) = struct.unpack(">i", buf.read(4))
            return list(struct.unpack(">%di" % n, buf.read(4 * n)))
        if tag == 12:
            (n,) = struct.unpack(">i", buf.read(4))
            return list(struct.unpack(">%dq" % n, buf.read(8 * n)))
        raise ValueError("bad tag %d" % tag)

    (t,) = struct.unpack(">b", buf.read(1))
    rd_name()
    return rd(t)


def unpack_heightmap(longs):
    """9 bits per entry, 7 entries per long, no spanning (1.16+ format)."""
    vals = []
    for L in longs:
        u = L & 0xFFFFFFFFFFFFFFFF
        for i in range(7):
            if len(vals) >= 256:
                break
            vals.append((u >> (9 * i)) & 0x1FF)
    return vals[:256]


def scan(path, limit=None, per_chunk=False):
    """(heights, chunk_count), or with per_chunk also a list of (cx, cz, heights)
    so a boundary can be located to the exact chunk."""
    data = open(path, "rb").read()
    heights = []
    rows = []
    chunks = 0
    for idx in range(1024):
        off = struct.unpack(">I", b"\0" + data[idx * 4:idx * 4 + 3])[0]
        cnt = data[idx * 4 + 3]
        if off == 0 or cnt == 0:
            continue
        start = off * 4096
        (length,) = struct.unpack(">I", data[start:start + 4])
        ctype = data[start + 4]
        raw = data[start + 5:start + 4 + length]
        try:
            nbt = read_nbt(io.BytesIO(zlib.decompress(raw) if ctype == 2 else raw))
        except Exception:
            continue
        lvl = nbt.get("Level", nbt)
        hm = (lvl.get("Heightmaps") or {}).get("WORLD_SURFACE")
        if not hm:
            continue
        vals = unpack_heightmap(hm)
        heights.extend(vals)
        if per_chunk:
            rows.append((lvl.get("xPos"), lvl.get("zPos"), vals))
        chunks += 1
        if limit and chunks >= limit:
            break
    if per_chunk:
        return heights, chunks, rows
    return heights, chunks


def summarise(vals):
    v = sorted(vals)
    return (v[0], v[len(v) // 2], v[-1], sum(v) / len(v),
            100.0 * sum(1 for x in v if x >= 250) / len(v))


if __name__ == "__main__":
    if "--per-chunk" in sys.argv:
        argv = [a for a in sys.argv if a != "--per-chunk"]
        h, n, rows = scan(argv[1], per_chunk=True)
        rows.sort(key=lambda r: (r[0] or 0, r[1] or 0))
        print("%-10s %-8s %6s %7s %5s %7s" % ("chunkX", "blockX", "min", "median", "max", "%>=250"))
        for cx, cz, vals in rows:
            if cz not in (0, None):
                continue  # one row of chunks is enough to see a boundary
            mn, med, mx, mean, pct = summarise(vals)
            print("%-10s %-8s %6d %7d %5d %6.1f%%" % (cx, (cx or 0) * 16, mn, med, mx, pct))
        sys.exit(0)

    h, n = scan(sys.argv[1])
    if not h:
        print("no heightmap data found")
        sys.exit(1)
    h.sort()
    solid = [v for v in h if v > 0]
    print("chunks=%d samples=%d" % (n, len(h)))
    print("min=%d  p25=%d  median=%d  p75=%d  max=%d  mean=%.1f"
          % (h[0], h[len(h) // 4], h[len(h) // 2], h[3 * len(h) // 4], h[-1],
             sum(h) / len(h)))
    print("at_or_above_250=%.1f%%  zero/air_columns=%.1f%%"
          % (100.0 * sum(1 for v in h if v >= 250) / len(h),
             100.0 * (len(h) - len(solid)) / len(h)))
