# Trithemius
`Trithemius` is a Java command line tool that implements [Johannes Trithemius](http://en.wikipedia.org/wiki/Johannes_Trithemius)' simple polyalphabetic cipher. It can encipher and decipher input from the standard in or a file, and release the output to the standard out or a file.

## Known bugs
* If you choose the same file as input and output, the file will simply end up empty, and there's no warning that this will happen
* Both input and output files must be readable and writable---but the input file should only have to be readable and the output writable
* I don't have access to Trithemius' original _Polygraphiae_ text, so I'm not sure if enciphering and deciphering are shifting in the right directions
