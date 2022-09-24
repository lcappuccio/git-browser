# GitBrowser

Neo4J database with REST API backend to perform searches in git repositories.

[![Neo4J for Git](https://github.com/lcappuccio/git-browser/actions/workflows/build.yml/badge.svg)](https://github.com/lcappuccio/git-browser/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lcappuccio_git-browser&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lcappuccio_git-browser)

Requirements:
* Java 11 (pending Neo4J 5.0 for JDK17)

# Testing Resources

* Uses own git repository as specified in `repository.folder` of the test `application.properties`.
* Enclosed Postman collection
* Swagger documentation http://localhost:8080/swagger-ui.html (**DISABLED**)

## References

* https://eclipse.org/jgit/documentation/
* https://github.com/centic9/jgit-cookbook

## ToDo

* add frontend (https://graphalchemist.github.io/Alchemy/#/ ?)