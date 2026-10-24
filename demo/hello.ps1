# WinScript Support 高亮验证脚本
<# 块注释
   跨多行 #>
param(
    [string]$Name = 'WinScript'
)

$env:PATH | Out-Null
$script:Count = 0
${braced name} = 'braced variable'

function Get-Greeting {
    param([string]$Who)
    return "Hello, $Who!"
}

$text = @"
Here-string（双引号，多行）
second line
"@

$literal = @'
Here-string（单引号，字面量）
'@

$items = Get-Process | Where-Object { $_.Name -match 'idea' -and $_.CPU -gt 1 }
foreach ($item in $items) {
    try {
        Write-Host ('{0}: {1}' -f $item.Name, $item.CPU)
    } catch {
        throw "failed: $_"
    }
}
