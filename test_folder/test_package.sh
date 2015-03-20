
find . -not -name 'test_package.sh' | xargs rm -Rf

cp ../test-tools-package/target/*.tar .

tar xvf *.tar
