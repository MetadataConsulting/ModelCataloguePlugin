#!/bin/bash

echo "Removing all IntelliJ IDEA settings"

find . -name "*.iml" -type f -delete
find . -name .idea -exec rm -rf {} \;