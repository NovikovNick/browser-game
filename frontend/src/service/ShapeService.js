const playerShape = [
    [-50.0, -50.0],
    [50.0, -50.0],
    [50.0, 50.0],
    [-50.0, 50.0]
]

const wallShape = [
    [-50.0, -50.0],
    [50.0, -50.0],
    [50.0, 50.0],
    [-50.0, 50.0]
]

const bulletShape = [
    [-5, -5],
    [5, -5],
    [5, 5],
    [-5, 5]
]

export function getPlayerShape() {
    return [...playerShape]
}

export function getWallShape() {
    return [...wallShape]
}

export function getBulletShape() {
    return [...bulletShape]
}