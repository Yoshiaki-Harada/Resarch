(TeX-add-style-hook
 "boilerplate"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("ujreport" "11pt" "a4paper" "fleqn")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("graphicx" "dvipdfmx")))
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperref")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperimage")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperbaseurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "nolinkurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
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
    "sotsuron"
    "here"
    "graphicx"
    "amsmath"
    "mathtools"
    "enumerate"
    "hyperref")
   (LaTeX-add-bibliographies
    "soturon"))
 :latex)

