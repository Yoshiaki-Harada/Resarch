(TeX-add-style-hook
 "sotsuron"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("ujreport" "11pt" "a4paper" "fleqn")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("graphicx" "dvipdfmx")))
   (TeX-run-style-hooks
    "latex2e"
    "newenv"
    "title"
    "abstract_eng"
    "abstract_jap"
    "main"
    "thanks"
    "ujreport"
    "ujreport11"
    "here"
    "graphicx"
    "amsmath"
    "mathtools"
    "enumerate")
   (LaTeX-add-bibliographies
    "soturon"))
 :latex)

