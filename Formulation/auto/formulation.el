(TeX-add-style-hook
 "formulation"
 (lambda ()
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("ujarticle" "uplatex")))
   (TeX-add-to-alist 'LaTeX-provided-package-options
                     '(("otf" "deluxe") ("pxchfon" "sourcehan")))
   (TeX-run-style-hooks
    "latex2e"
    "ujarticle"
    "ujarticle10"
    "amsmath"
    "otf"
    "pxchfon"
    "nccmath"
    "bm"))
 :latex)

