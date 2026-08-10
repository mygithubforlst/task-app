# 项目规范索引

> 用户称呼：NREC（上下文防丢），每次回复前必须带上称呼
> 自动触发：编码时自动读取对应规范文件验证

| 触发场景 | 关键词 | 规范文件 |
|---------|-------|---------|
| git提交 | commit、git | doc/git-commit规范.md |
| 接口设计 | 接口、rest、controller | doc/restful接口设计规范.md |
| 数据库 | db、数据库、jdbc | doc/数据库jdbc开发规范.md、doc/数据库jdbc连接配置说明.md |
| 日志打印 | log、日志 | doc/JAVA日志打印规范.md |
| 配置中心 | config、配置 | doc/配置中心说明.md |
| 包结构 | 包、structure | doc/项目包目录结构说明.md |
| 服务部署 | deploy、部署 | doc/服务部署说明.md |
| 服务配置 | serve、配置 | doc/服务配置说明.md |
| 过滤器 | filter、过滤器 | doc/过滤器接口设计.md |

**使用原则**：涉及以上关键词时，自动Read对应规范文档校验代码。核心遵循阿里巴巴Java手册。
