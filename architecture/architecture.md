# AutoBuild Architecture

A CI/CD Platform should be able to communicate to VCS and it should be capable of spawning up different virtual environments to run the checks.

For communicating with VCS, REST APIs are to be exposed.
For the Isolated Virtual Environment, Docker can be used.

Seems like the connection between Controller and Processor requires async communication. RabbitMQ can be used as a message broker (Not on priority rn).

## Sequence Diagram
![pipeline seq](./drawio/img/sequence.svg)

## Block Diagram
![](./drawio/img/block.svg)