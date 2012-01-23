all: pdflatex

pdflatex: rapport.tex
	pdflatex rapport.tex
	pdflatex rapport.tex
	evince rapport.pdf

bibtex: rapport.tex
	pdflatex rapport.tex
	bibtex rapport.aux
	pdflatex rapport.tex
	pdflatex rapport.tex
	evince rapport.pdf

clean:
	rm *.aux *.idx *.log *.lot *.out *.toc *.lof *.bbl *.blg
