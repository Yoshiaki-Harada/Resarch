(TeX-add-style-hook
 "boilerplate"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("ujreport" "11pt" "a4paper" "fleqn" "dvipdfmx")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("hyperref" "hidelinks")))
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
    "amsmath"
    "mathtools"
    "enumerate"
    "graphicx"
    "color"
    "hyperref"
    "pxjahyper")
   (TeX-add-symbols
    "maxwidth"
    "maxheight")
   (LaTeX-add-bibliographies
    "soturon"))
 :latex)

