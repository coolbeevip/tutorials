查询所有作者

```
query {
  getAllAuthor {
    id
    name
    posts {
      id
      title
      text
      category
    }
  }
}

```

增加作者

```
mutation {
  addAuthor(id: "2", name: "Thomas") {
    id
    name
  }
}
```

查询作者

```
query {
  getAuthor(authorId: "1"){
    id
    name
    posts {
      id
      title
      text
      category
    }
  }
}
```

增加文章

```
mutation {
  writePost(title: "title-2", text: "text2", category: "c1", authorId: "1") {
    id
    title
  }
}
```

查询文章

```
query {
  recentPosts(count: 3, offset: 0) {
    id
    title
    category
    author {
      id
      name
    }
  }
}
```

