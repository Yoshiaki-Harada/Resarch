(TeX-add-style-hook
 "sample"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("jjspe" "twocolumn" "a4paper" "dvips")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("graphicx" "dvipdfmx")))
   (TeX-run-style-hooks
    "latex2e"
    "jjspe"
    "jjspe10"
    "graphicx"
    "amsmath"
    "amssymb"
    "times"
    "type1cm"))
 :latex)

