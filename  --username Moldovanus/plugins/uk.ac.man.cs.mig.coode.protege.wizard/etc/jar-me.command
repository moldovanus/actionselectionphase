#!/bin/sh

cd "`dirname "$0"`"
jar cmf meta-inf/MANIFEST.MF BasicWizardsLibrary.jar uk
rm -r uk
rm -r meta-inf
