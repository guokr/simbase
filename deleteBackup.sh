echo Are you sure delete all y/[n]:
read i
if [[ $i == 'y' ]]; then
	rm data/*.dmp
	rm data/*.bak
	rm *.idx
	rm log/*.log
	rm log/*.log.*
fi
