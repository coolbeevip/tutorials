# Tutorials

![build](https://github.com/coolbeevip/tutorials/workflows/tutorials-ci/badge.svg) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fcoolbeevip%2Ftutorials.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fcoolbeevip%2Ftutorials?ref=badge_shield)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/coolbeevip/tutorials/badge)](https://api.securityscorecards.dev/projects/github.com/coolbeevip/tutorials)

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fcoolbeevip%2Ftutorials.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fcoolbeevip%2Ftutorials?ref=badge_large)

Build & Test

```shell
./mvnw clean package
```

Build & Test on Apple Silicon M1

```shell
./mvnw clean package -Pmacos-m1
```

```plantuml
@startuml
'https://plantuml.com/use-case-diagram

:Main Admin: as Admin
(Use the application) as (Use)

User -> (Start)
User --> (Use)

Admin ---> (Use)

note right of Admin : This is an example.

note right of (Use)
A note can also
be on several lines
end note

note "This note is connected\nto several objects." as N2
(Start) .. N2
N2 .. (Use)
@enduml
```