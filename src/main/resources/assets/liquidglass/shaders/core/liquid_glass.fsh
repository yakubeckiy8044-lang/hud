#version 150

uniform vec2 Size;
uniform vec4 Panel;
uniform float Radius;
uniform float BlurStrength;

in vec2 vertexUv;
in vec4 vertexColor;
out vec4 fragColor;

float roundedBoxSdf(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 pixel = gl_FragCoord.xy;
    vec2 center = Panel.xy + Panel.zw * 0.5;
    vec2 halfSize = Panel.zw * 0.5;
    float distanceToPanel = roundedBoxSdf(pixel - center, halfSize, Radius);

    float fillAlpha = 1.0 - smoothstep(0.0, 1.0, distanceToPanel);
    float borderAlpha = 1.0 - smoothstep(0.0, 1.5, abs(distanceToPanel));

    if (fillAlpha <= 0.0) discard;

    vec2 uv = pixel / Size;
    vec3 glass = vec3(0.035, 0.045, 0.065);
    vec3 border = mix(vec3(0.25, 0.75, 1.0), vec3(0.80, 0.35, 1.0), uv.y);
    vec3 color = mix(glass, border, borderAlpha * 0.85);

    fragColor = vec4(color, fillAlpha * 0.92) * vertexColor;
}
