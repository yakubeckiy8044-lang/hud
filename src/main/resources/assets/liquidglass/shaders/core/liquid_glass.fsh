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
    vec3 glass = mix(vec3(0.055, 0.028, 0.095), vec3(0.025, 0.035, 0.080), uv.x);
    vec3 edge = mix(vec3(0.34, 0.24, 0.82), vec3(0.27, 0.78, 1.0), smoothstep(0.08, 0.92, uv.y));
    fragColor = vec4(mix(glass, edge, border * 0.85), fill * 0.92);
}
