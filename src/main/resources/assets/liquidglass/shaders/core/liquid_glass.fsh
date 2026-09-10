#version 150
uniform vec2 ScreenSize;
uniform vec4 Panel;
uniform float Radius;
in vec2 vertexUv;
out vec4 fragColor;
float roundedBoxSdf(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}
void main() {
    vec2 pixel = gl_FragCoord.xy;
    vec2 center = Panel.xy + Panel.zw * 0.5;
    vec2 halfSize = Panel.zw * 0.5;
    float d = roundedBoxSdf(pixel - center, halfSize, Radius);
    float fill = 1.0 - smoothstep(0.0, 1.0, d);
    float border = 1.0 - smoothstep(0.0, 1.5, abs(d));
    if (fill <= 0.0) discard;
    vec2 uv = pixel / ScreenSize;
    vec3 glass = vec3(0.035, 0.045, 0.065);
    vec3 edge = mix(vec3(0.25, 0.75, 1.0), vec3(0.80, 0.35, 1.0), uv.y);
    fragColor = vec4(mix(glass, edge, border * 0.85), fill * 0.92);
}