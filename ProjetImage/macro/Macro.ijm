
function action(input, filename, output){
	open(input + filename);
	run("PlugindeBase (Not normalized)", "cs=[org.scijava.convert.DefaultConvertService [priority = 0.0]] ops=[net.imagej.ops.DefaultOpService [priority = 0.0]] colorimage=" + filename + " maxangle=20.0 outputdirectory=" + output + "");
}

input = getDirectory("Choose a image directory ");

output = getDirectory("choose a output directory ");

setBatchMode(true);
list = getFileList(input);
for (i = 0; i<list.length; i++)
	action(input, list[i], output);
setBatchMode(false);