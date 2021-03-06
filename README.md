# WSS

WSS, or Website Style Scripts, is a little project I've made that can convert CSS (Allowing for nested elements, as how SCSS does it) into usable HTML.

This can create nearly any webpage in existence, and to prove so I've remade the [Creative Tim Material Kit landing page example](https://demos.creative-tim.com/material-kit/examples/landing-page.html) into CSS, which compiles to a perfect replica.

The CSS may be found here: [landing-page.scss](https://github.com/RubbaBoy/WSS/tree/master/input/material-kit/landing-page.scss)

And its compiled output here: [landing-page.html](https://htmlpreview.github.io/?https://github.com/RubbaBoy/WSS/blob/master/input/material-kit/landing-page.html)

Here's another example input:

```scss
html {
    head {
        title {
            content: 'My Title'
        }
    }

    body {
        h1, #top {
            content: 'This Is A Title';
            background-color: green;
            html-title: 'The paragraph title';
        }

        p, .class1, .class2, #id {
            color: blue;
            content-0: 'Left paragraph ';

            a {
                content: 'followed by a';
                html-href: 'https://github.com/RubbaBoy/CSSWebsite';
            }

            content-2: ' right paragraph.';
            content-3: 'Some more content in the paragraph, featuring ';

            a {
                content: 'another link';
                html-href: 'https://github.com/RubbaBoy/CSSWebsite';
            }

            content-5: ' right about ';

            a {
                content: 'here';
                html-href: 'https://github.com/RubbaBoy/CSSWebsite';
            }

            content-7: '.';
        }
    }
}
```

The above output:

```html
<html>
  <head>
    <title>My Title</title>
  </head>
  <body>
    <h1 id="top" title="The paragraph title" style="background-color: green;">This Is A Title</h1>
    <p id="id" class="class1 class2" style="color: blue;">
        Left paragraph 
      <a href="https://github.com/RubbaBoy/CSSWebsite">followed by a</a>
         right paragraph.
        Some more content in the paragraph, featuring 
      <a href="https://github.com/RubbaBoy/CSSWebsite">another link</a>
         right about 
      <a href="https://github.com/RubbaBoy/CSSWebsite">here</a>
        .
    </p>
  </body>
</html>

```

