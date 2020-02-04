#!/bin/bash

sed -i '' 's/\/ui\/css/http:\/\/bozhen.live\/css/g' ./ui/index.html
sed -i '' 's/\/ui\/js/http:\/\/bozhen.live\/js/g' ./ui/index.html
