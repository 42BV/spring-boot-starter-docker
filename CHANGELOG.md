# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2018-12-19
### Changed
- Project is now built with java 1.8 to keep backwards compatibility with this jvm.

## [1.0.1.JAVA.11] - 2018-12-18
### Fixed
- Added @Configuration on DockerPostgresAutoConfiguration class; 
Spring > 5.1.x does not load beans from nested classes when top level class has no @Configuration.

## [1.0.0.JAVA.11] - 2018-12-18
### Changed
- Project is built with java 11
- Dependencies upgraded to be able to run on java 11
