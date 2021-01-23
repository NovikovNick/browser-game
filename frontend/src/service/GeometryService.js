export function rotate(p, angleRadian, center) {
    const cos = Math.cos(angleRadian);
    const sin = Math.sin(angleRadian);

    const x = p[0];
    const y = p[1];

    const x0 = center[0];
    const y0 = center[1];

    return [
        x0 + (x - x0) * cos - (y - y0) * sin,
        y0 + (y - y0) * cos + (x - x0) * sin
    ];
}