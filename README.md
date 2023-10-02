# Whydah-StatisticsDashboard

![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cantara/Whydah-StatisticsDashboard) ![Build Status](https://jenkins.cantara.no/buildStatus/icon?job=Whydah-StatisticsDashboard) ![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Cantara/Whydah-StatisticsDashboard) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active)  [![Known Vulnerabilities](https://snyk.io/test/github/Cantara/Whydah-StatisticsDashboard/badge.svg)](https://snyk.io/test/github/Cantara/Whydah-StatisticsDashboard)

![image](https://github.com/Cantara/Whydah-StatisticsDashboard/assets/842543/02afa67b-ba2c-42b2-a306-aa5e6a14b78d)


Dashboard to get some insights into the user activities for a running whydah installation

* Total number of users
* Total number of applications
* Number of new users/day  (per hour?)
   From which aplication
* Number of logins/day
   From which aplication
* Number of deleted users/day

  Number of sessions accessed/day/application

   - and more to come

## Configure environment

You can configure the environment by modifying json file `./environment_config.json` in the current directory

The name is the deployment target, and favicon should match the target

Example
```
{
  "environment_name": "My awesome target",
  "favicon": "awesome.favicon.ico"
}
```
