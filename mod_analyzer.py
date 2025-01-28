import re
import json
import glob
import os
from datetime import datetime
from collections import defaultdict

class ModAnalyzer:
    def __init__(self):
        # 存储标准模组列表
        self.standard_mods = {
            "forge": "47.3.0",
            "minecraft": "1.20.1",
            "chessmod": "1.1.3",
            "salwayseat": "5.2",
            "rhino": "2001.2.3-build.6",
            "kuma_api": "20.1.9-SNAPSHOT",
            "create_things_and_misc": "1.0.0",
            "zombiekit": "2.0.1",
            "xenon": "0.3.31",
            "rubidium": "0.7.1",
            "embeddium": "0.3.31",
            "geckolib": "4.4.8",
            "forgeskyboxes": "0.0.2-1.20.2",
            "playeranimator": "1.0.2-rc1+1.20",
            "morejs": "0.10.0",
            "jei": "15.20.0.105",
            "tacz": "1.0.3",
            "gundurability": "1.0-SNAPSHOT",
            "doomsday": "1.0",
            "placebo": "8.6.1",
            "travelersbackpack": "9.1.16",
            "animationjs": "1.20.1-0.0.9",
            "plasmovoice": "2.0.10",
            "lightmanscurrency": "1.20.1-2.2.4.1b",
            "mixinextras": "0.3.6",
            "balm": "7.3.11",
            "bsf": "3.0",
            "genshinstrument": "4.0.2",
            "cloth_config": "11.1.136",
            "sona": "1.1.0",
            "emotecraft": "2.2.7-b.build.50",
            "packmenu": "6.1.2",
            "hasoook": "1.3.1",
            "farmersdelight": "1.20.1-1.2.5",
            "cards": "1.0.2",
            "team_capes": "1.0.0",
            "terrablender": "3.0.1.7",
            "liosplayeranimatorapi": "0.0.8",
            "juris_fun_stuff": "1.3.3",
            "mafia_mod": "0.0.7",
            "curios": "5.10.0+1.20.1",
            "flywheel": "0.6.11-13",
            "create": "0.5.1.i",
            "powerfuljs": "1.6.1",
            "oculus": "1.7.0",
            "camera": "1.20.1-1.0.19",
            "kubejsdelight": "1.1.2",
            "securitycraft": "1.9.12",
            "dooms": "1.0.0",
            "playdate": "2.0.0",
            "hardcorerevival": "12.0.4",
            "liosmultiloaderutils": "1.2.4",
            "customnpcs": "1.20.1.20240629",
            "resourcefullib": "2.1.1",
            "worldedit": "7.2.15+6463-5ca4dff",
            "lootjs": "1.20.1-2.12.0",
            "automobility": "0.4.2+1.20.1-forge",
            "football": "1.3.4",
            "architectury": "9.2.14",
            "kubejs": "2001.6.5-build.16",
            "ponderjs": "1.4.0",
            "eventjs": "1.2.2",
            "kubejsoffline": "4.0.2",
            "kubejs_create": "2001.2.5-build.2",
            "buckshotroulette": "1.0.0",
            "dragnvehicles": "1.20.1-1.3"
        }
        
        # 存储玩家模组信息
        self.player_mods = defaultdict(dict)
        # 存储IP地址信息
        self.ip_players = defaultdict(set)
        # 存储玩家登录历史
        self.player_history = defaultdict(list)
        
    def parse_logs(self, log_paths):
        for log_path in log_paths:
            try:
                self.parse_log(log_path)
            except Exception as e:
                print(f"处理文件 {log_path} 时出错: {str(e)}")
            
    def parse_log(self, log_path):
        current_player = None
        reading_mods = False
        
        # 尝试不同的编码
        encodings = ['gbk', 'gb2312', 'utf-8']
        
        for encoding in encodings:
            try:
                with open(log_path, 'r', encoding=encoding) as f:
                    print(f"使用 {encoding} 编码读取文件: {log_path}")
                    for line in f:
                        # 检测玩家登录
                        login_match = re.search(r'\[(.*?)\] \[Server thread/INFO\] \[net\.minecraft\.server\.players\.PlayerList/\]: (.*?)\[/(.*?):', line)
                        if login_match:
                            timestamp = login_match.group(1)
                            player_name = login_match.group(2)
                            ip_address = login_match.group(3)
                            
                            current_player = player_name
                            # 记录玩家登录信息
                            login_info = {
                                'timestamp': timestamp,
                                'ip_address': ip_address
                            }
                            self.player_history[current_player].append(login_info)
                            # 记录IP地址关联的玩家
                            self.ip_players[ip_address].add(player_name)
                            continue

                        # 检测模组信息开始
                        if '已安装模组' in line and current_player:
                            reading_mods = True
                            self.player_mods[current_player]['mods'] = {}
                            continue

                        # 读取模组信息
                        if reading_mods and current_player:
                            mod_match = re.search(r'- (.*?):(.*?)$', line)
                            if mod_match:
                                mod_name = mod_match.group(1)
                                mod_version = mod_match.group(2)
                                self.player_mods[current_player]['mods'][mod_name] = mod_version
                            elif '光影包' in line:
                                reading_mods = False
                return  # 如果成功读取，就退出循环
            except UnicodeDecodeError:
                continue  # 如果编码错误，尝试下一个编码
            except Exception as e:
                print(f"读取文件时发生错误: {str(e)}")
                raise
        
        raise Exception(f"无法使用任何已知编码读取文件: {log_path}")

    def analyze_non_standard_mods(self):
        results = {}
        
        for player, data in self.player_mods.items():
            non_standard = {}
            if 'mods' in data:
                for mod_name, version in data['mods'].items():
                    if mod_name not in self.standard_mods:
                        non_standard[mod_name] = version
            
            if non_standard:
                # 获取玩家最近的登录信息
                login_history = self.player_history[player]
                latest_login = login_history[-1] if login_history else {'timestamp': 'Unknown', 'ip_address': 'Unknown'}
                
                results[player] = {
                    'login_time': latest_login['timestamp'],
                    'ip_address': latest_login['ip_address'],
                    'non_standard_mods': non_standard,
                    'login_history': login_history
                }
        
        return results

    def analyze_ip_addresses(self):
        suspicious_ips = {}
        for ip, players in self.ip_players.items():
            if len(players) > 1:  # 如果一个IP对应多个玩家
                suspicious_ips[ip] = list(players)
        return suspicious_ips

    def save_results(self, results, ip_analysis, output_file):
        combined_results = {
            'player_analysis': results,
            'ip_analysis': ip_analysis
        }
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(combined_results, f, ensure_ascii=False, indent=2)

    def print_results(self, results, ip_analysis):
        print("\n=== 非标准模组分析报告 ===")
        for player, data in results.items():
            print(f"\n玩家: {player}")
            print(f"最近登录时间: {data['login_time']}")
            print(f"IP地址: {data['ip_address']}")
            print("登录历史:")
            for login in data['login_history']:
                print(f"  - 时间: {login['timestamp']}, IP: {login['ip_address']}")
            print("安装的非标准模组:")
            for mod_name, version in data['non_standard_mods'].items():
                print(f"  - {mod_name}: {version}")

        print("\n=== IP地址分析报告 ===")
        if ip_analysis:
            print("\n发现以下IP地址被多个玩家使用：")
            for ip, players in ip_analysis.items():
                print(f"\nIP地址: {ip}")
                print("关联玩家:")
                for player in players:
                    print(f"  - {player}")
        else:
            print("\n未发现可疑的IP地址使用情况")

def main():
    analyzer = ModAnalyzer()
    
    # 获取日志文件路径
    log_pattern = input("请输入日志文件路径模式（支持通配符，如 *.log）: ")
    log_files = glob.glob(log_pattern)
    
    if not log_files:
        print("未找到匹配的日志文件！")
        return
        
    print(f"\n找到 {len(log_files)} 个日志文件:")
    for log_file in log_files:
        print(f"- {log_file}")
    
    # 分析所有日志文件
    analyzer.parse_logs(log_files)
    
    # 分析非标准模组
    results = analyzer.analyze_non_standard_mods()
    
    # 分析IP地址
    ip_analysis = analyzer.analyze_ip_addresses()
    
    # 打印结果
    analyzer.print_results(results, ip_analysis)
    
    # 保存结果到JSON文件
    output_file = "mod_analysis_results.json"
    analyzer.save_results(results, ip_analysis, output_file)
    print(f"\n分析结果已保存到: {output_file}")

if __name__ == "__main__":
    main() 