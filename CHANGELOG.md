# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 0.7.0

### Added

Reaction.getMetabolitesView method allows to get all left and right metabolites in
the same collection.

### Fixed

ReactionAttributes.getSideCompounds now returns a BioCollection of BioMetabolite instead
of a Set of String


## 0.6.0

### Removed
- MetExploreXml support : classes and apps

## [Unreleased]
### Added
- Graph Analysis Apps: chokepoints, distance matrix, subnetwork extraction, loadpoint, metaborank, scopecompounds and other utilities
- Improve graph export to gml: attributes can be exported too
### Removed
- FlexFlux
### Fixed
- Reaction graph creation was very slow, now fixed
- Fixed FloydWarshall undirected with multi-graphs