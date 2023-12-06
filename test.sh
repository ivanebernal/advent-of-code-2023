file_name=$(ls $1/$2| grep "\.kts$")
kotlinc -script $1/$2/$file_name -- -i $1/test_input.txt