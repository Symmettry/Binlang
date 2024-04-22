# Binlang

Esoteric programming language using the building blocks `and` and `not`.
A project inspired by [Sebastian Lague's video](https://www.youtube.com/watch?v=QZwneRb-zqA).

## How To Run
If you're in IntelliJ, make a configuration with arguments as `C:/path/to/file.bin`\
If you use a jar it's `java -jar --enable-preview C:/path/to/Binlang.jar C:/path/to/file.bin`

## Syntax

### Built-in Identifiers
Some built-in identifiers are included:

`and` checks if both values are 1, and will return 1 for yes and 0 for no.
`not` will invert the bit. 0 = 1, 1 = 0.
```
0 and 1; // 0
0 not; // 1
```
`#def` will let you define custom symbols (functions) that will run similar to others.
E.g., `0 nand b` or `0 something`. The last value that is emitted will be returned.\
The symbol can take in 1 or 2 arguments with syntax `#def symbol a {}` and `#def symbol a, b {}`
```
#def nand a, b {}
```
`#prn` will print a value to the console.\
`#pnl` will print a value to the console & include a new line.\
`#pnc` will print out a characters from binary.
```
#prn 0;
#pnl 1;
#pnc [[1;0;0;1;0;0;0;][1;1;0;1;0;0;1;]] // Hi
```
`#set` will set a variable. Variables must be all alphabetical.
```
#set a = 0;
```
`#con` will check if a number is 1.
```
#set a = 1;
#con a {
    #pnc [[1;1;1;1;0;0;1;][1;1;0;0;1;0;1;][1;1;1;0;0;1;1;]]; // yes
} {
    #pnc [[1;1;0;1;1;1;0;][1;1;0;1;1;1;1;]] // no
}
```
### Values

#### Numbers
Valid numbers are 0 and 1 excluding the index value when reading from arrays.
```
#prn 0
#prn 1
```

#### Arrays
Arrays can be formed with square brackets.\
A semicolon must separate all values (excluding arrays), and this includes the last one.\
You can get values from an array with arr[index;] -- this is the exception in which you can use numbers above 1.\
You cannot use arr[index;] in an operation, and must assign it to a variable first.\
Note: You shouldn't place a semicolon after an array.
```
#set arr = [0;1;a;b;c;]
#set valA = arr[0;]; // 0
```

#### Comments
Comments are made with 2 slashes.
```
#set a = 0; // this code so bad fr
```

## Examples
You can view examples in the `/examples/` folder.