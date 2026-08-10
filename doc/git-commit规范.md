# Git Commit Message 编写规范

Git Commit 时填写的 Message 需要遵循 **Conventional Commits** 规约。

## 规范简介

**Conventional Commits** 是一种用于给提交信息增加人机可读含义的规范。  
它是一种基于消息的轻量级约定，提供了一组用于创建清晰提交历史的简单规则，使得编写基于规范的自动化工具变得更加容易。

该约定与 **SemVer（语义化版本）** 相吻合，在提交信息中描述新特性、Bug 修复和破坏性变更。

## 提交样板

```text
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### 字段说明

- **`<type>`**：提交类型，冒号为英文冒号，冒号后需加一个空格。
- **`[optional scope]`**：影响范围（可选），用圆括号包裹。
- **`<description>`**：简短描述，相当于标题。
- **`[optional body]`**：详细描述（可选），与上一行之间需空一行。
- **`[optional footer(s)]`**：注脚信息（可选），如关闭 Issue 或 `BREAKING CHANGE`，与上文之间需空一行。

## Type 列表

| 类型         | 说明                                |
|------------|-----------------------------------|
| `feat`     | 新增功能（对应 SemVer 的 **MINOR** 版本）    |
| `fix`      | 修复 Bug（对应 SemVer 的 **PATCH** 版本）  |
| `docs`     | 仅文档更改                             |
| `style`    | 不影响代码含义的更改（如空格、格式、分号等）            |
| `refactor` | 代码重构（既不修复 Bug 也不新增功能）             |
| `perf`     | 性能优化相关的代码更改                       |
| `test`     | 添加缺失测试或修正现有测试                     |
| `build`    | 影响构建系统或外部依赖的更改（如 gulp、npm 等）      |
| `ci`       | 持续集成配置或脚本的更改（如 Travis、CircleCI 等） |
| `chore`    | 其他不修改 `src` 或 `test` 文件的杂项任务      |
| `revert`   | 回退某个提交                            |

## Scope（作用域）

可选字段，用于提供额外上下文。例如：

```text
feat(alarm): 增加告警列表导出 Excel 功能
```

## BREAKING CHANGE

若提交引入了 **破坏性 API 变更**（对应 SemVer 的 **MAJOR** 版本），需在正文或脚注开头注明：

```text
BREAKING CHANGE: <description>
```

> **注意**：使用 `standard-version` 等工具时，包含 `BREAKING CHANGE` 的提交会自动触发主版本号升级（如 `1.3.1` → `2.0.0`）。

## 关闭 Issue

如果变更来自 ClearQuest 并已同步到 Git 项目的 Issue，在提交 Footer 中应添加如下行以自动关闭 Issue：

```text
closes #12
```

## 提交示例

### 示例一

```text
fix($compile): couple of unit tests for IE9

Older IEs serialize html uppercased, but IE9 does not...
Would be better to expect case insensitive, unfortunately jasmine does not allow to use regexps for throw expectations.

closes #392
```

### 示例二

```text
style: validate rule configuration

BREAKING CHANGE: Due to additional validation while reading commitlint config, previously ignored rule settings are now considered critical errors when starting the CLI. The new behaviour is designed to help developers find issues with their configuration quicker.
```

### 示例三

```text
docs: add missing character (#612)

Fixes spelling of the word "for" in the docs: https://conventional-changelog.github.io/commitlint/#/concepts-commit-conventions
```

### 示例四

```text
chore: use non-fixed lerna version (#2026)

- switch config to reflect v3 usage
- use maypr versions for peerdeps
```