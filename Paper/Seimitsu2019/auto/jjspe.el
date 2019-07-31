(TeX-add-style-hook
 "jjspe"
 (lambda ()
   (TeX-run-style-hooks
    "latex2e"
    "ujarticle"
    "ujarticle10")
   (TeX-add-symbols
    '("dummyspace" 1)
    "footnotesize"
    "footnoterule"
    "thanks"
    "maketitle"
    "title"
    "author"
    "date"
    "and"
    "keywords"
    "correspondence"
    "etitle"
    "eauthor"
    "thefootnote"))
 :latex)

