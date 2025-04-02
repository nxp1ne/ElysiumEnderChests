# ElysiumEnderChests

**ElysiumEnderChests** is a lightweight Minecraft plugin for version 1.16.5 that adds portable Ender Chests to your server.

![Version](https://img.shields.io/badge/version-1.1-blue)
![Minecraft](https://img.shields.io/badge/Minecraft-1.16.5-green)
![API](https://img.shields.io/badge/API-Paper-yellow)

## Features

- Portable Ender Chests that players can use anytime, anywhere
- Fully customizable with gradient colors and HEX support
- Command-based access to own or other players' Ender Chests
- Permission-based system for administration
- Custom cooldown system with configurable delay
- Sound and title effects when opening the chest
- Performance optimizations (caching and efficient resource usage)
- Cooldown saving/loading for server restarts

## Commands

- `/enderchest` (or `/ec`) - Open your Ender Chest
- `/enderchest <player>` - Open another player's Ender Chest (with permission)
- `/enderchest give <player> [amount]` - Give portable Ender Chest(s) to a player
- `/enderchest reload` - Reload the plugin configuration

## Permissions

- `elysiumec.use` - Allows usage of portable Ender Chest items (default: true)
- `elysiumec.enderchest.see.own` - Allows opening own Ender Chest with /ec command (default: true)
- `elysiumec.enderchest.see.*` - Allows opening any player's Ender Chest (default: op)
- `elysiumec.enderchest.see.<playername>` - Allows opening a specific player's Ender Chest
- `elysiumec.admin` - Full access to all plugin commands and functions (default: op)

## Configuration

The plugin is highly configurable. You can customize:

- Item appearance (name, lore, glow)
- Cooldown timers and messages
- Sounds when opening chests
- Titles and subtitles when viewing chests
- All messages with HEX color support
- Performance settings (caching, cooldown saving)

Example config:
```yaml
item_enderchest:
  name: '&#b302fb&lE&#b902f9&ln&#c002f6&ld&#c602f4&le&#cc01f1&lr&#d301ef&l-&#d901ed&lC&#df01ea&lh&#e601e8&le&#f200e3&ls&#f900e0&lt'
  lore:
    - ''
    - ' &7- &fPortable Ender Chest that'
    - '     &fcan help you anytime!'
    - ''
    - ' &#3afb00● &f- Activate with right-click'
    - ''
  glow: false
  sound:
    enabled: true
    open: BLOCK_ENDER_CHEST_OPEN
    volume: 1.0
    pitch: 1.0
```

## Installation

1. Download the latest version of ElysiumEnderChests
2. Place the .jar file in your server's plugins folder
3. Restart your server or use `/reload`
4. Configure the plugin in the config.yml file
5. Use `/enderchest reload` to apply changes

## Support

If you encounter any issues or have questions, please contact the developer. (Telegram: @p1nemkl)
---

# ElysiumEnderChests (Русский)

**ElysiumEnderChests** - это легкий плагин для Minecraft версии 1.16.5, который добавляет переносные эндер-сундуки на ваш сервер.

## Возможности

- Переносные эндер-сундуки, которые игроки могут использовать в любое время и в любом месте
- Полностью настраиваемый интерфейс с поддержкой градиентных цветов и HEX
- Доступ к своему или чужому эндер-сундуку через команды
- Система разрешений для администрирования
- Настраиваемая система задержек между использованиями
- Звуковые и визуальные эффекты при открытии сундука
- Оптимизации производительности (кэширование и эффективное использование ресурсов)
- Сохранение/загрузка задержек при перезагрузке сервера

## Команды

- `/enderchest` (или `/ec`) - Открыть свой эндер-сундук
- `/enderchest <игрок>` - Открыть эндер-сундук другого игрока (с разрешением)
- `/enderchest give <игрок> [количество]` - Выдать переносной эндер-сундук(и) игроку
- `/enderchest reload` - Перезагрузить конфигурацию плагина

## Разрешения

- `elysiumec.use` - Разрешает использовать предметы переносных эндер-сундуков (по умолчанию: true)
- `elysiumec.enderchest.see.own` - Разрешает открывать свой эндер-сундук через команду /ec (по умолчанию: true)
- `elysiumec.enderchest.see.*` - Разрешает открывать эндер-сундук любого игрока (по умолчанию: op)
- `elysiumec.enderchest.see.<имя_игрока>` - Разрешает открывать эндер-сундук конкретного игрока
- `elysiumec.admin` - Полный доступ ко всем командам и функциям плагина (по умолчанию: op)

## Настройка

Плагин имеет широкие возможности настройки. Вы можете изменить:

- Внешний вид предмета (название, описание, свечение)
- Таймеры и сообщения о задержке
- Звуки при открытии сундуков
- Заголовки и подзаголовки при просмотре сундуков
- Все сообщения с поддержкой HEX-цветов
- Настройки производительности (кэширование, сохранение задержек)

Пример конфигурации:
```yaml
item_enderchest:
  name: '&#b302fb&lЭ&#b902f9&lн&#c002f6&lд&#c602f4&lе&#cc01f1&lр&#d301ef&l-&#d901ed&lс&#df01ea&lу&#e601e8&lн&#f200e3&lд&#f900e0&lу&#ff00de&lк'
  lore:
    - ''
    - ' &7- &fЭндер-сундук который может'
    - '     &fпомочь в любое время!'
    - ''
    - ' &#3afb00● &f- Активация на ПКМ.'
    - ''
  glow: false
  sound:
    enabled: true
    open: BLOCK_ENDER_CHEST_OPEN
    volume: 1.0
    pitch: 1.0
```

## Установка

1. Скачайте последнюю версию ElysiumEnderChests
2. Поместите .jar файл в папку plugins вашего сервера
3. Перезапустите сервер или используйте `/reload`
4. Настройте плагин в файле config.yml
5. Используйте `/enderchest reload` для применения изменений

## Поддержка

Если у вас возникли проблемы или вопросы, пожалуйста, свяжитесь с разработчиком. (Телеграм: @p1nemkl)