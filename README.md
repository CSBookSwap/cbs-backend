<div align="center">

![CS Book Swap](https://raw.githubusercontent.com/CSBookSwap/.github/main/org_resources/images/logocon_mini.png)

# CS Book Swap-API

[![Java CI](https://github.com/CSBookSwap/cbs-backend/actions/workflows/java-ci.yml/badge.svg?branch=dev)](https://github.com/CSBookSwap/cbs-backend/actions/workflows/java-ci.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/324fe2ee927644c0abf66088829cb14d)](https://app.codacy.com/gh/CSBookSwap/cbs-backend/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![codecov](https://codecov.io/gh/CSBookSwap/cbs-backend/graph/badge.svg?token=CIFKTIY2AN)](https://codecov.io/gh/CSBookSwap/cbs-backend)
[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](https://github.com/CSBookSwap/.github/blob/main/LICENSE)
</div>

---
<details style="font-size: large;"><summary>Table of Contents</summary>

<div style="margin-left: 10%;">

- [About](#about)
- [Getting Started](#getting-started)
-
    - [Prerequisites](#prerequisites)
-
    - [Installation](#installation)
-
    - [Testing](#testing)
-
    - [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

</div>

</details>

## About

CS Book Swap is a web application that allows students to buy, sell and swap textbooks with each other. This repository
contains the backend API for the application.



> For more information about the project, please see the CS Book Swap [Organization](https://github.com/CSBookSwap)
> page.

---

## Getting Started

### Prerequisites

| Name                                                                  | Version | Description                                                  |
|-----------------------------------------------------------------------|---------|--------------------------------------------------------------|
| [Java](https://adoptium.net/temurin/releases/?package=jdk&version=21) | 21 LTS  | I'm using latest Java LTS version from Eclipse Temurin.      |
| [Maven](https://maven.apache.org/download.cgi)                        | 3.9.5   | You can use the Maven Wrapper included in this project.      |
| [Docker](https://docs.docker.com/get-docker/)                         | 20.10.8 | Docker is required to run environment for integration tests. |

<details><summary>Dependencies</summary>

<table>
<thead>
<tr>
<th align="center">Name</th>
<th align="center">Version</th>
</tr>
</thead>
<tbody>
<tr>
<td>Spring Web</td>
<td align="center" rowspan="5">3.0.2</td>
</tr>
<tr>
<td>Spring JDBC</td>
</tr>
<tr>
<td>Spring Web</td>
</tr>
<tr>
<td>Spring Security</td>
</tr>
<tr>
<td>Spring Testcontainers</td>
</tr>
<tr>
<td> PostgreSQL Driver</td>
<td align="center">42.6.0</td>
</tr>
<tr>
<td>Flyway Core</td>
<td align="center">9.22.3</td>
</tr>
<tr>
<td> Testcontainers Junit Jupiter</td>
<td align="center" rowspan="2">1.19.3</td>
</tr>
<tr>
<td> Testcontainers Postgres</td>
</tr>
</tbody>
</table>


</details>

### Installation

1. Clone the repo
    ```bash
    git clone https://github.com/CSBookSwap/cbs-backend.git && cd cbs-backend
    ```
2. Install dependencies
    ```bash 
    mvn clean install
    ```

### Testing

Run the tests with Maven (Docker is required to run integration tests with Testcontainers):

```bash
./mvnw clean test
```

Jacoco test coverage report will be generated in `target/site/jacoco/index.html`

### Deployment

1. Run the application

```bash
./mvnw spring-boot:run
```

2. Swagger UI will be available at `http://localhost:8080/swagger-ui.html`
3. You can also use the Postman collection to test the API. It's available in the `postman` directory.

---

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create.
Any contributions you make are **greatly appreciated**.
For more information, please see the [CONTRIBUTING](./CONTRIBUTING.md) file in this repository.

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FCSBookSwap%2Fcbs-backend.svg?type=small)](https://app.fossa.com/projects/git%2Bgithub.com%2FCSBookSwap%2Fcbs-backend?ref=badge_small)

This project is licensed under the MIT License — see the [LICENSE](./LICENSE) file for details.

---


