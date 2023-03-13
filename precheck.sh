#!/usr/bin/env bash

sbt clean scalastyle scalafmt test:scalafmt it:test::scalafmt test it:test scalafmtCheckAll
