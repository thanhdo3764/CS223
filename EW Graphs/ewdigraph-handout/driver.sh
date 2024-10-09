#!/bin/bash
set -e
set -o errtrace

trap 'err_report' ERR

TARGET=${1:-run}

# driver.sh - The simplest autograder using JUnitTests.
#   Usage: ./driver.sh

FAILJSON=$(printf "import json\nwith open('wsuvtest.json') as fin:\n  r = fin.read()\n  s=json.loads(r)['scores']\nfor k in s:\n  s[k]=0.0\nprint(json.dumps({'scores': s}, sort_keys=True))\n"  | python3)

err_report() {
    ERR_CODE=$?
    if [ -e wsuv.autolab.WeightedScoreListener.out ]
      then cat wsuv.autolab.WeightedScoreListener.out
    fi
    echo "Error! ($ERR_CODE)"
    echo ${FAILJSON}
    exit 0
}

# Compile the code

echo "Verifying submission"
make --no-print-directory verify

echo "Cleaning..."
make --no-print-directory clean

echo "Compiling..."
rm -rf ${BUILD}
make --no-print-directory bin

echo "Running..."
make --no-print-directory ${TARGET}

if [ -e wsuv.autolab.WeightedScoreListener.out ]
  then cat wsuv.autolab.WeightedScoreListener.out
fi


exit 0
