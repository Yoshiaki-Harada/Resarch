%
% 精密工学会誌用 LaTeX2e クラスファイル
% manuscript-1.93 を使用して作成
% 
\ProvidesClass{jjspe}[2002/08/06 maeda] 
% modified: n.umezu 2014/01/10
%
%\LoadClassWithOptions{jarticle} 
\LoadClassWithOptions{ujarticle} 
%
%  double space setting
%
%\def\baselinestretch{1.5}
%
%
%  title page setting
%
  \if@titlepage
  \renewcommand\maketitle{\begin{titlepage}%
  \let\footnotesize\small
  \let\footnoterule\relax
  \let \footnote \thanks
  \null\vfil
  \vskip 60\p@
  \begin{center}%
    {\LARGE \bf \@title \par}%
    \vskip 3em%
    {\Large
     \lineskip .75em%
      \begin{tabular}[t]{c}%
        \@author
      \end{tabular}\par}%
      \vskip 1.5em%
    {\large
     \lineskip .75em%
      \begin{tabular}[t]{c}%
        \@eauthor
      \end{tabular}\par}%
      \vskip 1.5em%
    {\large \@date \par}%       % Set date in \large size.
  \end{center}\par
  \@thanks
  \vfil\null
  \end{titlepage}%
  \setcounter{footnote}{0}%
  \global\let\thanks\relax
  \global\let\maketitle\relax
  \global\let\@thanks\@empty
  \global\let\@author\@empty
  \global\let\@date\@empty
  \global\let\@title\@empty
  \global\let\title\relax
  \global\let\author\relax
  \global\let\date\relax
  \global\let\and\relax
%  \global\let\affiliation\relax
  \global\let\keywords\relax
%  \global\let\correspondence\relax
 \global\let\etitle\relax 
 \global\let\eauthor\relax 
}
\else
\renewcommand\maketitle{\par
  \begingroup
    \renewcommand\thefootnote{\@fnsymbol\c@footnote}%
    \def\@makefnmark{\rlap{\@textsuperscript{\normalfont\@thefnmark}}}%
    \long\def\@makefntext##1{\parindent 1em\noindent
            \hb@xt@1.8em{%
                \hss\@textsuperscript{\normalfont\@thefnmark}}##1}%
    \if@twocolumn
      \ifnum \col@number=\@ne
        \@maketitle
      \else
        \twocolumn[\@maketitle]%
      \fi
    \else
      \newpage
      \global\@topnum\z@   % Prevents figures from going at top of page.
      \@maketitle
    \fi
    \thispagestyle{empty}\@thanks
  \endgroup
  \setcounter{footnote}{0}%
  \global\let\thanks\relax
  \global\let\maketitle\relax
  \global\let\@maketitle\relax
  \global\let\@thanks\@empty
  \global\let\@author\@empty
  \global\let\@date\@empty
  \global\let\@title\@empty
  \global\let\title\relax
  \global\let\author\relax
  \global\let\date\relax
  \global\let\and\relax
%  \global\let\affiliation\relax
  \global\let\keywords\relax
%  \global\let\correspondence\relax
 \global\let\etitle\relax 
 \global\let\eauthor\relax 
}
\def\@maketitle{\newpage
 \null
 \vskip 26pt \begin{center}
 {\LARGE \bf \@title \par} \vskip 9pt {\large %\lineskip .5em
\begin{tabular}[t]{l}\Large \@author 
 \end{tabular}
 \par} 
% \vskip 1em {\large \lineskip .5em 
%\begin{tabular}[t]{l}\@affiliation \end{tabular} \par}
 \vskip 16pt
\begin{tabular}[t]{c}\@etitle
 \end{tabular}
 \vskip 6pt
\begin{tabular}[t]{c}\@eauthor 
 \end{tabular}
\end{center}
 \vskip 6pt
  \centerline{\box\@abstractbox}
\@ifundefined{@keywords}{}{
% \begin{center}
%  {\bf Key words: \vspace{-.5em}\vspace{0pt}}

% \end{center}
\hspace{25.5pt}
\begin{minipage}[t]{154mm}
\setlength{\parindent}{1zw}
\setlength{\baselineskip}{12pt}
{{\bf Key words: } \@keywords}
\end{minipage}
 \vskip 26pt}
%\@ifundefined{@correspondence}{}{
% \begin{center}
%  {\bf correspondence to \vspace{-.5em}\vspace{0pt}}
% \end{center}
% \begin{quotation} \@correspondence \end{quotation}
% \vskip 1.5em}
% \gdef\@affiliation{}
 \gdef\@keywords{}
 \gdef\@correspondence{}
\gdef\@etitle{} 
\gdef\@eauthor{} 
} 
%\long\def\affiliation#1{\long\gdef\@affiliation{#1}}
\long\def\keywords#1{\long\gdef\@keywords{#1}}
\long\def\correspondence#1{\long\gdef\@correspondence{#1}}
\long\def\etitle#1{\long\gdef\@etitle{#1}} 
\long\def\eauthor#1{\long\gdef\@eauthor{#1}} 
\fi 

\renewcommand{\abstractname}{}
\renewcommand{\refname}{参　考　文　献}
\renewcommand{\figurename}{Fig.}
\renewcommand{\tablename}{Table}
\renewcommand{\thefigure}{{\bf \@arabic\c@figure}}
\renewcommand{\thetable}{{\bf \@arabic\c@table}}

\renewcommand{\footnoterule}{%本文と脚注間の線を伸ばす
  \kern-3\p@
  \hrule width 35mm
  \kern 2.6\p@}

% figure で番号の後にコロンをつけない
\long\def\@makecaption#1#2{% \@makecaption を再定義します
%  \small
  \fontsize{7.5pt}{11pt}\selectfont
  \vskip\abovecaptionskip
  \iftdir\sbox\@tempboxa{#1\hskip1zw#2}%
    \else\sbox\@tempboxa{#1~ #2}% ここの : を ~ に変更する
  \fi
  \ifdim \wd\@tempboxa >\hsize% 
    \iftdir #1\hskip1zw#2\relax\par
%      \else #1~ #2\relax\par\fi% ここの : を ~ に変更する
      \else #1~ \parbox[t]{76mm}{#2}\relax\par\fi% ここの : を ~ に変更する
  \else
    \global \@minipagefalse
    \hbox to\hsize{\hfil\box\@tempboxa\hfil}% センタリング
%   \hbox to\hsize{\box\@tempboxa\hfil}%      左詰め
%   \hbox to\hsize{\hfil\box\@tempboxa}%      右詰め
  \fi
  \vskip\belowcaptionskip}

\setlength{\abovecaptionskip}{-1mm}
\setlength{\belowcaptionskip}{0mm}

% abstract
\newbox\@abstractbox
\renewenvironment{abstract}{%
  \global\setbox\@abstractbox\hbox\bgroup
  \begin{minipage}[b]{154mm}\par
    \normalsize\parindent1zw
    \setlength{\baselineskip}{12pt}
  }%
  {\end{minipage}\egroup}

% section をセンタリングする
\renewcommand{\section}{%
  \@startsection{section}% #1 見出し
   {1}% #2 見出しのレベル
   {\z@}% #3 横組みの場合，見出し左の空き(インデント量)
   {.3\Cvs \@plus.2\Cdp \@minus.2\Cdp}% #4 見出し上の空き
   {.3\Cvs \@plus.2\Cdp \@minus.2\Cdp}% #5 見出し下の空き (負の値なら見出し後の空き) 
%  {\reset@font\Large\bfseries}% #6 見出しの属性
   {\centering\reset@font\normalsize\bfseries}% 中央揃え
%  {\raggedleft\reset@font\Large\bfseries}% 右揃え
}%

\renewcommand{\subsection}{\@startsection{subsection}{2}{\z@}%
   {.1\Cvs \@minus.1\Cdp}%
   {.1\Cvs \@minus.1\Cdp}%
   {\reset@font\normalsize\bfseries}}

\renewcommand{\subsubsection}{\@startsection{subsubsection}{3}{\z@}%
   {.1\Cvs \@minus.1\Cdp}%
   {.1\Cvs \@minus.1\Cdp}%
   {\reset@font\normalsize}}

\renewcommand{\thesection}{\indent\@arabic\c@section.} % section の数字の後にピリオドをつける
\renewcommand{\thesubsection}{\thesection\@arabic\c@subsection}

%\renewcommand{\thefootnote}{\fnsymbol{footnote}}

% 最終ページの段の高さを揃えるためのマクロ
\newcommand{\dummyspace}[1]{
  \renewcommand{\footnoterule}{\relax}
  \begingroup  \makeatletter
  \def\thefootnote{\ifnum\c@footnote>\z@\@arabic\c@footnote\fi}
      \footnotetext{%
  	\vspace{#1}
      }%
  \endgroup
}

% 参考文献の項目間スペースを詰める
\renewenvironment{thebibliography}[1]
{\section*{\refname\@mkboth{\refname}{\refname}}%
   \small
   \list{\@biblabel{\@arabic\c@enumiv}}%
        {\settowidth\labelwidth{\@biblabel{#1}}%
         \itemsep\z@% この行を追加
         \parsep\z@%  この行も追加
         \leftmargin\labelwidth
         \advance\leftmargin\labelsep
         \@openbib@code
         \usecounter{enumiv}%
         \let\p@enumiv\@empty
         \renewcommand\theenumiv{\@arabic\c@enumiv}}%
   \sloppy
   \clubpenalty4000
   \@clubpenalty\clubpenalty
   \widowpenalty4000%
   \sfcode`\.\@m}
  {\def\@noitemerr
    {\@latex@warning{Empty `thebibliography' environment}}%
   \endlist}

\newdimen\@Q\@Q=0.25mm
\renewcommand{\normalsize}{
\@setfontsize\normalsize{\@ixpt}{13.0pt}
\abovedisplayskip 9.25\@Q \@plus3\@Q \@minus3\@Q
\abovedisplayshortskip \z@ \@plus2\@Q
\belowdisplayshortskip 3\@Q \@plus2\@Q \@minus2\@Q
\belowdisplayskip \abovedisplayskip
\let\@listi\@listI}
\normalsize
\renewcommand{\small}{%
\@setfontsize\small{\@viiipt}{9.0pt}
\abovedisplayskip 8\@Q \@plus2\@Q \@minus2\@Q
\abovedisplayshortskip \z@ \@plus2\@Q
\belowdisplayshortskip 3\@Q \@plus2\@Q \@minus2\@Q
\belowdisplayskip \abovedisplayskip}
\renewcommand{\footnotesize}{\@setfontsize\footnotesize{\@viiipt}{9.0pt}}
\renewcommand{\scriptsize}{\@setfontsize\scriptsize{\@vipt}{9\@Q}}
\renewcommand{\tiny}{\@setfontsize\tiny{\@vpt}{7\@Q}}
\renewcommand{\large}{\@setfontsize\large{\@ixpt}{14.0pt}}
\renewcommand{\Large}{\@setfontsize\Large{\@xpt}{16.0pt}}
\renewcommand{\LARGE}{\@setfontsize\LARGE{\@xivpt}{21.0pt}}
\renewcommand{\huge}{\@setfontsize\huge{\@xviipt}{32\@Q}}
\renewcommand{\Huge}{\@setfontsize\Huge{\@xxpt}{36\@Q}}

% 余白関係の設定
\setlength{\baselineskip}{13.0pt}
%\setlength{\textheight}{253mm}
\setlength{\textheight}{55\baselineskip}
\setlength{\columnsep}{8.95mm}
\setlength{\textwidth}{172mm}
\setlength{\textwidth}{59zw}%%
%%
%\setlength{\footheight}{0.0in}
\setlength{\topmargin}{0in}
\setlength{\headheight}{0.0in}
\setlength{\headsep}{0.0in}
\setlength{\voffset}{-3.4mm}
\setlength{\oddsidemargin}{-6.4mm}
\setlength{\parindent}{1zw}
\setlength{\parskip}{0.0mm}

%
%  pagestyle setting
%
\pagestyle{empty} 
% 
%  omit_number = 0 
% 
%  citation_form = 2 
%
%  citation form setting
%
%\def\@cite#1#2{$^{\hbox{\scriptsize{#1\if@tempswa , #2\fi})}}$} 
\def\@cite#1{$^{\hbox{\scriptsize{#1})}}$} 
% 
%  biblio_heading = 2 
%
%  bibliography heading setting
%
\renewcommand*{\@biblabel}[1]{#1)\hfill} 
% end of jjspe.cls ----------------------------------------------- 
