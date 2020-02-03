on ReadAllExternal(filename)
    return read filename as «class utf8»
end ReadAllExternal

on MacChooseFileOfType(typesStr)
    local typeList

    set backup to AppleScript's text item delimiters
    set AppleScript's text item delimiters to {","}
    set typeList to every text item of typesStr
    set AppleScript's text item delimiters to backup

    try
        return POSIX path of (choose file of type typeList)
    on error number -128
        # user cancelled
        return ""
    end try
end MacChooseFileOfType

on MacChooseFile(defaultValue)
    try
        try
            return POSIX path of (choose file default location defaultValue)
        on error number -1700
            # file not exist
            return POSIX path of (choose file)
        end try
    on error number -128
        # user cancelled
        return defaultValue
    end try
end MacChooseFile

on MacChooseApp(defaultValue)
    try
        return (choose application)'s id
    on error number -128
        # user cancelled
        return defaultValue
    end try
end MacChooseApp

on MacChooseFolder(defaultValue)
    try
        try
            return POSIX path of (choose folder default location defaultValue)
        on error number -1700
            # file not exist
            return POSIX path of (choose folder)
        end try
    on error number -128
        # user cancelled
        return defaultValue
    end try
end MacChooseFolder

on MacExecute(str)
    return do shell script ("(" & str & ") &> /dev/null; echo $?")
end MacExecute
