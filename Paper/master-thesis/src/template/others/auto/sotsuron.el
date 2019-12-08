(TeX-add-style-hook
 "sotsuron"
 (lambda ()
   (TeX-add-symbols
    "footnotesize"
    "footnoterule"
    "thanks"
    "maketitle"
    "tightlist")
   (LaTeX-add-environments
    "abstract"))
 :latex)

