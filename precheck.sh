#!/usr/bin/env bash

sbt clean scalastyle scalafmt test:scalafmt it:test::scalafmt coverage test it:test scalafmtCheckAll coverageReport
