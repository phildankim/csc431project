CSC431 Fall 2018
Dr. Keen

Daniel Kim
Michael Hilomen

==INSTRUCTIONS FOR OUR COMPILER==
Make sure to make clean;make before running!

To run stack-based:
	java MiniCompiler [input].mini -stack [-simp]
Note: The -simp flag enables optimizations and can only be used with the -stack flag.

To run register-based:
	java MiniCompiler [input].mini -llvm [-sscp] [-uce] 
Note: The -sscp and -uce flags enables optimizations and can only be used with the -llvm flag. You can use either or both.

To run all benchmarks (from given_parser/):
	Stack w/o opt:
		./run_stack
	Stack w/ opt:
		./run_stack_simp

	Register w/o opt:
		./run_llvm
	Register w/ UCE:
		./run_llvm_uce
	Register w/ UCE and SSCP:
		./run_llvm_uce_sscp
Note: These are all with the .longer inputs. These scripts also assume that the benchmark dir is within given_parser.