# Java Particle Simulator

A toy Java particle simulation that visualizes particles being attracted toward their collective center of mass. The force approximation is based on distance from the center of mass, which produces emergent clustering and visual patterns.

## Features

- Interactive particle spawning.
- Variable-mass particles.
- Bulk particle patterns such as circles and grids.
- Color changes based on distance from the center of mass.
- Simple visual experimentation with gravitational-style attraction.

## Controls

| Key | Action |
| --- | --- |
| `1` | Create a particle with mass `1` at the cursor position. |
| `2` | Create a particle with mass `1000` at the cursor position. |
| `3` | Create a circle of particles around the center of the screen. |
| `4` | Create a grid of particles across the screen. |
| `5` | Clear all particles and reset the simulation. |

## Running locally

Open the project in a Java IDE and run the main application entry point. If the project is converted to a standard Gradle or Maven layout later, add the exact build command here.

## Demo

![](https://media.giphy.com/media/XClIzqMU53ihRTGw4V/giphy.gif)

![](https://media.giphy.com/media/RgtoFGf8SkISeDq54p/giphy.gif)

## Notes

This is an experimental visual simulation, not a physically accurate N-body solver. The goal is interactive behavior and interesting emergent motion rather than numerical precision.

## License

No license has been selected yet.
