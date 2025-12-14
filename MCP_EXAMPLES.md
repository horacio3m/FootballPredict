# Exemplos de Uso do MCP

## Endpoints

### GET /mcp/tools
Retorna a lista de tools disponíveis.

**Resposta:**
```json
[
  {
    "name": "get_last_match",
    "description": "Retorna o último jogo de um time, incluindo data e resultado",
    "inputSchema": {
      "type": "object",
      "properties": {
        "team": {
          "type": "string",
          "description": "Name of the team"
        }
      },
      "required": ["team"]
    }
  },
  {
    "name": "get_matches",
    "description": "Retorna partidas filtradas por time e/ou intervalo de datas",
    "inputSchema": {
      "type": "object",
      "properties": {
        "team": {
          "type": "string",
          "description": "Name of the team (optional)"
        },
        "dateFrom": {
          "type": "string",
          "description": "Start date in YYYY-MM-DD format (optional)"
        },
        "dateTo": {
          "type": "string",
          "description": "End date in YYYY-MM-DD format (optional)"
        }
      },
      "required": []
    }
  }
]
```

### POST /mcp/query

#### Consulta em Linguagem Natural

**Request:**
```json
{
  "query": "Quando foi o último jogo do Vasco?"
}
```

**Response:**
```json
{
  "response": "O último jogo do Vasco foi em...",
  "toolResults": null
}
```

#### Execução Direta de Tool

**Request:**
```json
{
  "toolCalls": [
    {
      "name": "get_last_match",
      "arguments": {
        "team": "Vasco"
      }
    }
  ]
}
```

**Response:**
```json
{
  "response": "Tool execution completed",
  "toolResults": [
    {
      "date": "2024-12-10T20:00:00Z",
      "status": "FINISHED",
      "homeTeam": "Vasco",
      "awayTeam": "Flamengo",
      "homeScore": 2,
      "awayScore": 1
    }
  ]
}
```

#### Múltiplas Tools

**Request:**
```json
{
  "toolCalls": [
    {
      "name": "get_matches",
      "arguments": {
        "team": "Flamengo",
        "dateFrom": "2025-01-01",
        "dateTo": "2025-01-31"
      }
    }
  ]
}
```

**Response:**
```json
{
  "response": "Tool execution completed",
  "toolResults": [
    {
      "filters": {...},
      "resultSet": {...},
      "matches": [...]
    }
  ]
}
```

