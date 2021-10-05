#!/bin/bash

function signInJar(){
    local f

    echo "$1"

    # TODO : Prevent gathering META-INF folder
    jar tf "$1" | grep '\.so\|\.dylib\|\.jnilib\|\.jar'  > filelist.txt

    while read f
    do
        f=$(printf %q "$f")
        
        jar xf "$1" "$f"

        if [[ "$f" =~ .*".jar" ]]; then
            mkdir "_sub" && cd "_sub"

            signInJar "../$f"
            cd ".." && rm -fr "_sub"
        else
            codesign --remove-signature -v "$f"
            codesign --timestamp --force -s "$MACOS_DEV_ID" -v "$f"
            echo "---"
        fi

        jar uf "$1" "$f"
    done < filelist.txt
}

find ./ -name "*jar" > jarlist.txt

# Sign .jar files
while read j
do
    signInJar "$j"
    
    find . -not -wholename "*Gama.app*" -delete

    echo "xxx"
done < jarlist.txt

# Sign single lib files
find ./ \( -name "*dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign --remove-signature -v {} \;
find ./ \( -name "*dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign --timestamp --force -s "$MACOS_DEV_ID" -v {} \;

# Sign GAMA compiled file
codesign --entitlements "$GITHUB_WORKSPACE/artifacts/entitlements.plist" --timestamp --options=runtime --force -s "$MACOS_DEV_ID" -v ./Gama.app/Contents/MacOS/Gama