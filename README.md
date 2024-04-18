# Binlang

Esoteric programming language using the building blocks `and` and `not`.
A project inspired by [Sebastian Lague's video](https://www.youtube.com/watch?v=QZwneRb-zqA).

## Syntax

### Built-in Identifiers
Some built-in identifiers are included:

`and` checks if both values are 1, and will return 1 for yes and 0 for no.
`not` will invert the bit. 0 = 1, 1 = 0.
```
0 and 1;
0 not;
```
`#def` will let you define custom symbols (functions) that will run similar to others.
E.g., `0 nand b` or `0 something`. The last value that is emitted will be returned.
The symbol can take in 1 or 2 arguments with syntax `#def symbol a {}` and `#def symbol a, b {}`
```
#def nand a, b {}
```
`#prn` will print to the console.
`#pnl` will print to the console & include a new line.
```
#prn 0
#pnl 1
```
`#set` will set a variable. Variables must be all alphabetical.
```
#set a = 0;
```
### Values

#### Numbers
Valid numbers are 0 and 1 with the exception of arrays.
```
#prn 0
#prn 1
```

#### Arrays
Arrays can be formed with square brackets.
A semicolon must separate all values, and this includes the last one.
You can get values from an array with arr[index;] -- this is the exception in which you can use numbers above 1.
You cannot use arr[index;] in an operation, and must assign it to a variable first.
```
#set arr = [0; 1; a; b; c;];
#set valA = arr[0;];
```

## Examples
You can view examples in the `/examples/` folder.