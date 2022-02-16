# Changelog CompanyApp
>All notable changes to this project will be documented in this file

## [released]

## [1.1.0] - 16-02-22
### Added
- new Changelog
- new automated mode for operating the application automatically
- Class and package `de.iqwrwq.auto.AutoApplicationBot` added
- new `isMoving` property for ship-connections
- new `fullAutoMode` property in `config/config.properties`
- Kernel mode changing by reading the property value `fullAutoMode= true/false`

### Changed
- ShipApp sends back `moving` request to notify server to change property `isMoving`