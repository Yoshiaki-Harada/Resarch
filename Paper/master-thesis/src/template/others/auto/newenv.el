(TeX-add-style-hook
 "newenv"
 (lambda ()
   (TeX-add-symbols
    '("shaji" 2)
    '("secref" 1)
    '("pow" 1)
    '("lw" 1)
    '("namelistlabel" 1)
    "vle"
    "al"
    "ten"
    "makelabel")
   (LaTeX-add-environments
    '("namelist" 1)))
 :latex)

