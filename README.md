
# Java Particle Simulator
This project implements a toy particle simulator, that aims to approximate the gravitational interactions of an arbitrary amount of particles in an otherwise closed and empty system. The gravitational approximation is applied by attracting all individual particles to their collective center of mass, with a strength proportional to the inverse square root of each particles distance to the center of mass.

The color of each particle is based on the particles distance from the center of mass, which leads to some interesting visual patterns emerging.

There are several key bindings to interact with the simulation
- Pressing 1 on the keyboard will create a particle with a mass of 1 at the cursors location
- Pressing 2 on the keyboard will create a particle with a mass of 1000 at the cursors location
- Pressing 3 on the keyboard will create a create a circle of points around the center of the screen, one point at each degree around the circle
- Pressing 4 on the keyboard create a grid of particles on the entire screen, one at every 4th X & Y position starting at (0,0)
- Pressing 5 on the keyboard will clear all the particles in the simulation, effectively resetting it.

![](https://media.giphy.com/media/XClIzqMU53ihRTGw4V/giphy.gif)

![](https://media.giphy.com/media/RgtoFGf8SkISeDq54p/giphy.gif)
