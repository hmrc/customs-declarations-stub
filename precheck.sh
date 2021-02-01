#!/usr/bin/env bash

sbt clean scalafmt test:scalafmt scalastyle coverage test scalafmt::test test:scalafmt::test coverageReport
