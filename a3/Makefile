all: pdf

submit:
	marmoset_submit cs348 A3 join_graph.py

pdf:
	pdflatex a3.tex

clean:
	rm -rf __pycache__/ a3.pdf

.PHONY: all submit pdf clean
