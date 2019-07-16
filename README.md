# CSS Website

This is a little project I've made that can convert CSS (Allowing for nested elements, as how SCSS does it) into usable HTML.

Example input:

```scss
html {
    head {
        title {
            content: 'My Title'
        }
    }

    body {
        h1, #top {
            content: 'This Is A Title'
        }

        p, .class1, .class2, #id {
            content: 'This is a paragraph with some content'
        }
    }
}
```

Example output:

```html
<html>
  <head>
    <title>My Title</title>
  </head>
  <body>
    <h1 id="top">This Is A Title</h1>
    <p id="id" class="class1 class2">This is a paragraph with some content</p>
  </body>
</html>
```

