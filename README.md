# CSYE7200-Movie-Recommendation

## Goal
Build a scalable movie recommendation System to answer the question “What movie should I watch tonight?”. It will return some related movies based on the given movie.

## System Structure
modules of the system:

Controller: The frontend contains webserver, user interface, session management, authentication.

Model: The model contains main logic of generating response and manipulating data.

Datacenter: The data manager contains all calculations and low-level data management.

## Acceptance criteria: 
The system should return answers within 3 seconds and can handle maximum 10 requests per second.
