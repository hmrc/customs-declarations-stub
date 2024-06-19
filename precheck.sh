#!/usr/bin/env bash

sbt clean scalastyle scalafmt test:scalafmt test scalafmtCheckAll
