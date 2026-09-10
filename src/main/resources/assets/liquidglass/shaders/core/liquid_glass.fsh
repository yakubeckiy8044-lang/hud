#version 150
uniform vec2 ScreenSize;
uniform vec4 Panel;
uniform float Radius;
uniform sampler2D Background;
uniform int UseBackground;
in vec2 vertexUv;
out vec4 fragColor;
float roundedBoxSdf(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}
vec3 blurredBackground(vec2 uv) {
    vec2 texel = 1.35 / ScreenSize;
    vec3 color = texture(Background, uv).rgb * 0.227027;
    color += texture(Background, uv + vec2(texel.x, 0.0)).rgb * 0.194594;
    color += texture(Background, uv - vec2(texel.x, 0.0)).rgb * 0.194594;
    color += texture(Background, uv + vec2(0.0, texel.y)).rgb * 0.121621;
    color += texture(Background, uv - vec2(0.0, texel.y)).rgb * 0.121621;
    color += texture(Background, uv + texel).rgb * 0.054054;
    color += texture(Background, uv - texel).rgb * 0.054054;
    color += texture(Background, uv + vec2(texel.x, -texel.y)).rgb * 0.016216;
    color += texture(Background, uv + vec2(-texel.x, texel.y)).rgb * 0.016216;
    return color;
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
    vec2 local = clamp((pixel - Panel.xy) / Panel.zw, 0.0, 1.0);
    vec3 world = UseBackground == 1 ? blurredBackground(uv) : vec3(0.06, 0.04, 0.10);
    vec3 tint = mix(vec3(0.055, 0.025, 0.095), vec3(0.018, 0.030, 0.075), local.x);
    vec3 glass = mix(world, tint, 0.68);
    glass += vec3(0.035, 0.018, 0.060) * (1.0 - local.y) * 0.45;
    vec3 edge = mix(vec3(0.42, 0.24, 0.92), vec3(0.28, 0.76, 1.0), local.y);
    float innerGlow = (1.0 - smoothstep(0.0, 0.035, local.y)) * 0.10;
    fragColor = vec4(mix(glass, edge, border * 0.88) + edge * innerGlow, fill * 0.84);
}
